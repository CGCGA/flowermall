"""
【使用 Stable Difusion 生成花卉图片】
"""
import json
import base64

from database_api.Order import Order
from database_api.User import User
from database_api.Flower import Flower
# from database_api import r as redis

from tenacity import retry, stop_after_attempt, wait_exponential

import requests
from secret import API_KEY, QP_ID, USER_ID
API_URL = f'https://api-qpilot.woa.com/v1/chat/completions'
HEADERS = {'Content-Type': 'application/json',
           'QPilot-ID': QP_ID, 'Authorization': f'Bearer {API_KEY}'}
DATA_TEMPLATE = {'user': USER_ID, 'stream': False, 'model': 'QPilot-code',
                 'messages': [{'role': 'user', 'content': ''}]}


# 生成模型
GENERATE_MODEL = 'gpt-3.5-turbo'
# 默认温度
TEMPERATURE = 0.8


# 提示词模板
def prompt_template(flower: Flower) -> str:
    return f"""
```text
{str(flower)}
```

将上面 text 中的内容翻译为英文。下面给出英文翻译：
""".strip()


# 提交请求
def get_qpilot_response(**kwargs) -> str:
    """提交请求到 QPilot 服务器，如果没有则为空字符串
    :param kwargs: 请求参数
    :return: QPilot 服务器返回的结果 (assistant.content)
    """
    data = DATA_TEMPLATE.copy()
    data.update(kwargs)

    json = None
    try:
        response = requests.post(url=API_URL, headers=HEADERS, json=data)
        json = response.json()
        choices = json.get('choices', [])
        for choice in choices:
            message = choice.get('message', {})
            if message.get('role') == 'assistant':
                content = message.get('content')
                if content is not None:
                    return content.strip()
    except Exception as e:
        print(e, json)

    return ''


@retry(stop=stop_after_attempt(3), wait=wait_exponential(multiplier=1, min=4, max=70))
def get_generation(prompt: str, try_index=1) -> str:
    """根据提示词生成回答
    :param prompt: 提示词
    :param try_index: 尝试次数
    :return: 模型回答
    """

    # 根据尝试次数调整模型温度
    temperature = TEMPERATURE if try_index == 1 else 0.5
    temperature = 0.8 if try_index == 2 else 0.5
    temperature = 0.2 if try_index == 3 else 0.5

    outputs = get_qpilot_response(
        model=GENERATE_MODEL,
        messages=[
            {"role": "user", "content": prompt},
        ],
        temperature=temperature,
    )

    if len(outputs) == 0:
        raise Exception("生成回答失败")
    return outputs


def get_flower_generation(flower: Flower, try_index: int = 1) -> str:
    """根据 flower 生成回答
    :param flower: Flower
    :param try_index: 尝试次数
    :return: 模型回答
    """

    prompt = prompt_template(flower)
    try_num = 6
    for _ in range(try_num):
        try:
            generation = get_generation(prompt, try_index)
            return generation
        except:
            continue

    return ""


def main():
    all_flowers = Flower.get_all_from_redis()
    num = 0
    flist = [7, 9, 11, 13, 15, 17, 19, 21, 23, 25,
             27, 28, 30, 32, 34, 36, 38, 40, 42, 43]
    for flower in all_flowers:
        if num not in flist:
            num += 1
            continue
        print(f'编号 {num}')
        num += 1
        print(f'{flower.uuid}')
        generation = get_flower_generation(flower)
        print(f'{generation}')
        print("====================================")

    print(len(all_flowers))
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
