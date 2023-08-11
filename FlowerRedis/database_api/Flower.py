import redis
import json
import uuid
import random


# 导入 __init__.py 中的 r
from . import r


class Flower:
    def __init__(self, name, price, traits, synopsis, uuid_str=None):
        self.name = name
        self.price = price
        self.traits = traits
        self.synopsis = synopsis
        self.uuid = uuid_str if uuid_str else str(uuid.uuid4())

    def __str__(self):
        return f'Flower(name={self.name}, price={self.price}, traits={self.traits}, synopsis={self.synopsis})'

    def __repr__(self):
        return self.__str__()

    def to_dict(self):
        return {'name': self.name, 'price': self.price, 'traits': self.traits, 'synopsis': self.synopsis}
    
    def to_json(self):
        return json.dumps(self.to_dict())

    @staticmethod
    def from_dict(dic, uuid=None):
        return Flower(dic.get('name'), dic.get('price'), dic.get('traits'), dic.get('synopsis'), uuid)

    @staticmethod
    def from_json(json_str, uuid=None):
        dic = json.loads(json_str)
        return Flower.from_dict(dic, uuid)

    def add_to_redis(self):
        """添加到 Redis 数据库的 flowers 列表中"""
        r.lpush('flowers', self.to_json())
        r.set('flower:' + self.name, self.to_json())
        r.set('flower:' + self.name + ':uuid', self.uuid)
        r.sadd('flowers_set', self.name)
        r.set(self.uuid, self.to_json())
        r.lpush('flowers_uuid', self.uuid)

    def delete_from_redis(self):
        """从 Redis 数据库的 flowers 列表中删除"""
        string = r.get('flower:' + self.name)
        if string is None:
            print(f'Warning: {self.name} is not in Redis.')
            return
        r.lrem('flowers', 0, string)
        r.delete('flower:' + self.name)
        r.delete('flower:' + self.name + ':uuid')
        r.srem('flowers_set', self.name)
        r.delete(self.uuid)
        r.lrem('flowers_uuid', 0, self.uuid)

    def sync_to_redis(self):
        """同步到 Redis 数据库的 flowers 列表中"""
        self.delete_from_redis()
        self.add_to_redis()

    @staticmethod
    def get_all_from_redis() -> list:
        """从 Redis 数据库的 flowers 列表中获取所有商品"""
        flowers = []
        for uuid in r.lrange('flowers_uuid', 0, -1):
            flowers.append(Flower.find_by_uuid(uuid))
        return flowers

    @staticmethod
    def get_all_names_from_redis():
        """从 Redis 数据库的 flowers 列表中获取所有商品的 name"""
        return r.smembers('flowers_set')

    @staticmethod
    def get_all_uuids_from_redis():
        """从 Redis 数据库的 flowers 列表中获取所有商品的 uuid"""
        return r.lrange('flowers_uuid', 0, -1)

    @staticmethod
    def find_by_name(name: str):
        """根据 name 从 Redis 数据库的 flowers 列表中查找商品"""
        uuid = r.get('flower:' + name + ':uuid')
        if uuid:
            string = r.get(uuid)
            return Flower.from_json(string, uuid)
        else:
            return None

    @staticmethod
    def find_by_uuid(uuid: str):
        """根据 uuid 从 Redis 数据库的 flowers 列表中查找商品"""
        string = r.get(uuid)
        return Flower.from_json(string, uuid)

    def set_thumbnail(self, base64_image_str: str):
        """设置缩略图
        :param base64_image_str: base64 编码的图片字符串
        """
        r.set(self.uuid + ':thumbnail', base64_image_str)

    def set_pictures(self, base64_image_str_list: list):
        """设置图片列表
        :param base64_image_str_list: base64 编码的图片字符串列表
        """
        for base64_image_str in base64_image_str_list:
            r.lpush(self.uuid + ':pictures', base64_image_str)

    def get_thumbnail(self) -> str:
        """获取缩略图
        """
        if r.get(self.uuid + ':thumbnail') is None:
            # 如果没有缩略图则返回空字符串
            return ""
        return r.get(self.uuid + ':thumbnail')

    def get_pictures(self) -> list:
        """获取图片列表
        """
        if r.llen(self.uuid + ':pictures') == 0:
            # 如果没有图片则返回空列表
            return []
        return r.lrange(self.uuid + ':pictures', 0, -1)

    def delete_thumbnail(self):
        """删除缩略图"""
        r.delete(self.uuid + ':thumbnail')

    def delete_pictures(self):
        """删除图片列表"""
        r.delete(self.uuid + ':pictures')
