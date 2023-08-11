import redis
import json
import uuid
import random


# 导入 __init__.py 中的 r
from . import r

from .Flower import Flower


class Order:
    def __init__(self, id, user_id, flower_uuid, amount, time, status):
        self.id = id
        self.user_id = user_id
        self.flower_uuid = flower_uuid
        self.amount = amount
        self.time = time
        self.status = status

    def __str__(self):
        return f'Order(id={self.id}, user_id={self.user_id}, flower_uuid={self.flower_uuid}, amount={self.amount}, time={self.time}, status={self.status})'

    def __repr__(self):
        return self.__str__()

    def to_dict(self):
        return {'id': self.id, 'user_id': self.user_id, 'flower_uuid': self.flower_uuid, 'amount': self.amount, 'time': self.time, 'status': self.status}

    def to_json(self):
        return json.dumps(self.to_dict())

    @staticmethod
    def from_dict(dic: dict):
        """从字典中构造订单"""
        return Order(dic.get('id'), dic.get('user_id'),
                     dic.get('flower_uuid'), dic.get('amount'),
                     dic.get('time'), dic.get('status'))

    @staticmethod
    def from_json(string: str):
        """从 JSON 字符串中构造订单"""
        return Order.from_dict(json.loads(string))

    def add_to_redis(self):
        """添加到 Redis 数据库的 orders 列表中"""
        r.lpush('orders', self.to_json())
        r.set('order:' + self.id, self.to_json())
        r.sadd('orders_set', self.id)
        r.set('user:' + self.user_id + ':orders', self.to_json())

    def delete_from_redis(self):
        """从 Redis 数据库的 orders 列表中删除"""
        string = r.get('order:' + self.id)
        if string is None:
            print(f'Warning: order {self.id} does not exist.')
            return
        r.lrem('orders', 0, string)
        r.delete('order:' + self.id)
        r.srem('orders_set', self.id)
        r.delete('user:' + self.user_id + ':orders')

    def sync_to_redis(self):
        """同步到 Redis 数据库的 orders 列表中"""
        self.delete_from_redis()
        self.add_to_redis()

    @staticmethod
    def get_all_from_redis() -> list:
        """从 Redis 数据库的 orders 列表中获取所有订单"""
        orders = []
        for order in r.lrange('orders', 0, -1):
            orders.append(Order.from_json(order))
        return orders

    @staticmethod
    def generate_random_order(user_id):
        """生成随机订单"""
        id = str(uuid.uuid4())
        flower_uuid = random.choice(Flower.get_all_uuids_from_redis())
        amount = random.randint(1, 100)
        random_time = random.randint(0, 1000000000)
        status = random.choice(['待付款', '待发货', '待收货', '已完成', '已取消'])
        return Order(id, user_id, flower_uuid, amount, random_time, status)
