import redis
import json
import uuid
import random


# 导入 __init__.py 中的 r
from . import r

from .Order import Order


class User:
    def __init__(self, id, email, phone, intro):
        self.id = id
        self.email = email
        self.phone = phone
        self.intro = intro

    def __str__(self):
        return f'User(id={self.id}, email={self.email}, phone={self.phone}, intro={self.intro})'

    def __repr__(self):
        return self.__str__()

    def to_dict(self):
        return {'id': self.id, 'email': self.email, 'phone': self.phone, 'intro': self.intro}

    def to_json(self):
        return json.dumps(self.to_dict())

    @staticmethod
    def from_dict(dic):
        return User(dic.get('id'), dic.get('email'), dic.get('phone'), dic.get('intro'))

    @staticmethod
    def from_json(json_str):
        dic = json.loads(json_str)
        return User.from_dict(dic)

    def add_to_redis(self):
        """添加到 Redis 数据库的 users 列表中"""
        r.lpush('users', self.to_json())
        r.set('user:' + self.id, self.to_json())
        r.sadd('users_set', self.id)
        r.set('user:' + self.id + ':password_hash', hash('123456'))

    def delete_from_redis(self):
        """从 Redis 数据库的 users 列表中删除"""
        string = r.get('user:' + self.id)
        if string is None:
            print(f'Warning: {self.id} is not in Redis.')
            return
        r.lrem('users', 0, string)
        r.delete('user:' + self.id)
        r.srem('users_set', self.id)
        r.delete('user:' + self.id + ':password_hash')

    def sync_to_redis(self):
        """同步到 Redis 数据库的 users 列表中"""
        self.delete_from_redis()
        self.add_to_redis()

    def get_password_hash(self):
        """获取密码哈希值"""
        return r.get('user:' + self.id + ':password_hash')

    def set_shopping_cart(self, shopping_cart: list):
        """设置购物车
        :param shopping_cart: 购物车列表，包含商品的 UUID """
        r.set('user:' + self.id + ':shopping_cart', json.dumps(shopping_cart))

    def get_shopping_cart(self) -> list:
        """获取购物车"""
        return json.loads(r.get('user:' + self.id + ':shopping_cart'))

    @property
    def shopping_cart(self) -> list:
        """获取购物车"""
        return self.get_shopping_cart()

    @shopping_cart.setter
    def shopping_cart(self, shopping_cart: list):
        """设置购物车
        :param shopping_cart: 购物车列表，包含商品的 UUID """
        self.set_shopping_cart(shopping_cart)

    def get_orders(self) -> list:
        """获取订单"""
        string = r.get('user:' + self.id + ':orders')
        if string is None:
            # 如果没有订单则生成随机订单
            rand = random.randint(0, 20)
            orders = []
            for _ in range(rand):
                order = Order.generate_random_order(self.id)
                order.sync_to_redis()
                orders.append(order.to_json())
            return orders
        return json.loads(string)

    @property
    def orders(self) -> list:
        """获取订单"""
        return self.get_orders()

    @orders.setter
    def orders(self, orders: list):
        """设置订单
        :param orders: 订单列表 """
        r.set('user:' + self.id + ':orders', json.dumps(orders))

    @staticmethod
    def get_all_from_redis() -> list:
        """从 Redis 数据库的 users 列表中获取所有用户"""
        users = []
        for user in r.lrange('users', 0, -1):
            users.append(User.from_json(user))
        return users

    @staticmethod
    def get_all_ids_from_redis():
        """从 Redis 数据库的 users 列表中获取所有用户的 id"""
        return r.smembers('users_set')

    @staticmethod
    def find_by_id(id):
        """根据 id 从 Redis 数据库的 users 列表中查找用户"""
        if r.sismember('users_set', id):
            string = r.get('user:' + id)
            return User.from_json(string)
        else:
            return None
