"""
【装载数据到数据库】
将已经生成过滤的 JSON 数据装载到 Redis 数据库中。
"""
import json

from database_api.Order import Order
from database_api.User import User
from database_api.Flower import Flower
# from database_api import r as redis


def read_json(path: str) -> list:
    with open(path, 'r', encoding='utf-8') as f:
        return json.load(f)


def read_users() -> list:
    return read_json('data/json/filtered/users_info.json')


def read_flowers() -> list:
    return read_json('data/json/filtered/flowers_info.json')


def main():
    print('Loading users...')
    users = read_users()
    print('Loading flowers...')
    flowers = read_flowers()

    for user_json in users:
        print(f'Loading user {user_json.get("id")}...')
        user = User.from_dict(user_json)
        print('Adding user to Redis...')
        user.sync_to_redis()

    for flower_json in flowers:
        print(f'Loading flower {flower_json.get("name")}...')
        flower = Flower.from_dict(flower_json)
        print('Adding flower to Redis...')
        flower.sync_to_redis()

    for user in User.get_all_from_redis():
        print(f'User {user.id} has {len(user.orders)} orders.')

    print('Done.')
    pass


def test():
    for user in User.get_all_from_redis():
        print(f'User {user.id} has {len(user.orders)} orders.')

    for flower in Flower.get_all_from_redis():
        print(f'Flower {flower.name} is {flower.price}')

    for order in Order.get_all_from_redis():
        print(f'Order {order.id} is {order.status}')


if __name__ == '__main__':
    # main()
    test()
