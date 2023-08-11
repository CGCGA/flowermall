import json


def main():
    # 读取 `data/json/flowers_info.json` 文件
    users_info = None
    with open('data/json/flowers_info.json', 'r', encoding='utf-8') as f:
        users_info = json.load(f)

    # 过滤
    filtered = []
    st = set()
    if isinstance(users_info, list):
        users_info.reverse()
        for user in users_info:
            if not isinstance(user, dict):
                print(f'Warning: {user} is not a dict.')
                continue

            if not user.get('name'):
                print(f'Warning: {user} has no name.')
                continue

            if user.get('name') not in st:
                name = user.get('name')
                price = user.get('price')
                traits = user.get('traits')
                synopsis = user.get('synopsis')

                if not price or not traits or not synopsis:
                    print(f'Warning: {name} has empty fields.')
                    continue

                name = name.strip().strip('"')
                price = price.strip().strip('"')
                traits = traits.strip().strip('"')
                synopsis = synopsis.strip().strip('"')

                print(f'Info: {name} is added.')
                dic = {'name': name, 'price': price,
                       'traits': traits, 'synopsis': synopsis}
                filtered.append(dic)
                st.add(name)
            else:
                print(f'Warning: {user.get("name")} is repeated.')

    # 写入 `data/json/filtered/users_info.json` 文件
    with open('data/json/filtered/flowers_info.json', 'w', encoding='utf-8') as f:
        json_str = json.dumps(filtered, ensure_ascii=False)
        f.write(json_str)

    print('Done.')


if __name__ == '__main__':
    main()
