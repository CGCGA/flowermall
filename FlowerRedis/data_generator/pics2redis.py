"""
【装载图片数据到数据库】
将已经生成过滤的 JSON 数据装载到 Redis 数据库中。
"""
import json
import base64

from database_api.Order import Order
from database_api.User import User
from database_api.Flower import Flower
# from database_api import r as redis


def read_image2base64(path: str) -> str:
    with open(path, 'rb') as f:
        return base64.b64encode(f.read()).decode()


def main():

    print('Done.')
    pass


def test():
    all_flowers = Flower.get_all_from_redis()
    for flower in all_flowers:
        print(flower, flower.uuid)

    print(len(all_flowers))


if __name__ == '__main__':
    # main()
    test()
