"""
【用于生成用户数据】
使用 QPilot 网络 API ，将代码片段提交到 QPilot 服务器
对返回的结果进行解析，提取出代码片段的语义信息
如果可以解析则继续，否则重试最多三次
统一得到 JSON 列表格式的结果
"""

# 自行创建 secret.py 文件，包含 API_KEY, QP_ID, USER_ID

import re
import json
import requests
from secret import API_KEY, QP_ID, USER_ID
API_URL = f'https://api-qpilot.woa.com/v1/chat/completions'
HEADERS = {'Content-Type': 'application/json',
           'QPilot-ID': QP_ID, 'Authorization': f'Bearer {API_KEY}'}
DATA_TEMPLATE = {'user': USER_ID, 'stream': False, 'model': 'QPilot-code',
                 'messages': [{'role': 'user', 'content': ''}]}


# 生成模型
GENERATE_MODEL = 'QPilot-code'
# 默认温度
TEMPERATURE = 0.8
# 系统提示
SYSTEM_PROMT = """
Example YAML:
```yaml
- id: hk_shao
  email: m@shao.fun
  phone: 18712345678
  intro: I am a student.
- id: misaka
  email: sashdasj@qq.com
  phone: 18712225671
  intro: 我很快乐，是个程序员。
- id: 烧风
  email: hello_shao@qq.com
  phone: 13711225647
  intro: 爱喝咖啡，爱吃水果。
...
```

You need to generate some data for the database. 
Contains only id, email, phone, intro (Use Chinese, no more than 20 words).
Generate 20 YAML format data freely and creatively.
""".strip()


# 提示词模板
def prompt_template(code: str) -> str:
    """根据代码片段生成提示词
    :param code: 代码片段
    :return: 提示词
    """
    return f"""
{SYSTEM_PROMT}
{code}

New YAML data:
```yaml
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


def get_code_generation(code: str, try_index: int = 1) -> str:
    """根据代码片段生成回答
    :param code: 代码片段
    :param try_index: 尝试次数
    :return: 模型回答
    """

    prompt = prompt_template(code)
    generation = get_generation(prompt, try_index)
    return generation


def generation_yaml_parser(yaml_like_str: str) -> list:
    """解析模型输出的表格字符串
    :param yaml_like_str: YAML 字符串
    :return: 解析后的 JSON 对象
    """
    json_list = []
    tmp_dic = {}
    yaml_like_str += '\n-'
    for line in yaml_like_str.splitlines():
        line = line.strip()
        if len(line) == 0:
            continue

        if line.startswith('-'):
            if len(tmp_dic) != 0:
                json_list.append(tmp_dic)
            tmp_dic = {}
            line = line[1:].strip()
            if len(line) == 0:
                continue

        splits = line.split(':')
        if len(splits) != 2:
            continue

        key = None
        val = None
        if len(splits[0].strip()) != 0:
            key = splits[0].strip()
        if len(splits[1].strip()) != 0:
            val = splits[1].strip()

        if key is not None and val is not None:
            tmp_dic[key] = val

    return json_list


def get_code_generation_and_parse(prompt: str, try_num=3) -> list:
    """根据代码片段生成回答，并解析
    :param prompt: 代码片段
    :param try_num: 尝试次数
    :return: 解析后的 JSON 对象
    """
    json_list = []
    for i in range(try_num):
        try:
            generation = get_code_generation(prompt, i)
            json_list = generation_yaml_parser(generation)
        except:
            print(f'第 {i + 1} 次尝试失败')
            continue

        return json_list

    return json_list


if __name__ == '__main__':

    users = []

    for i in range(30):
        code = """""".strip()
        out = get_code_generation(code)
        print('模型输出：')
        print(out)
        parsed = generation_yaml_parser(out)
        print('解析结果：')
        print(parsed)

        users.extend(parsed)

    json_str = json.dumps(users, ensure_ascii=False)

    with open('data/json/users_info.json', 'w', encoding='utf-8') as f:
        f.write(json_str)


# 运行方法: `python3 data_generator/gen_users.py`
