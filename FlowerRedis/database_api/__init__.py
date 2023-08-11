import redis

r = redis.Redis(host='localhost', port=6379, decode_responses=True)
r.set('admin', 'HK-SHAO')  # 设置 name 对应的值
print('管理员名称', r.get('admin'))  # 取出键 name 对应的值
