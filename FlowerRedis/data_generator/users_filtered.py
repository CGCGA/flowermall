import json


def main():
    # 读取 `data/json/users_info.json` 文件
    users_info = None
    with open('data/json/users_info.json', 'r', encoding='utf-8') as f:
        users_info = json.load(f)

    # 过滤
    users_filtered = []
    st = set()
    if isinstance(users_info, list):
        users_info.reverse()
        for user in users_info:
            if not isinstance(user, dict):
                print(f'Warning: {user} is not a dict.')
                continue

            if not user.get('id'):
                print(f'Warning: {user} has no id.')
                continue

            if user.get('id') not in st:
                id = user.get('id').strip().strip('"')
                email = user.get('email').strip().strip('"')
                phone = user.get('phone').strip().strip('"')
                intro = user.get('intro').strip().strip('"')

                if not id or not email or not phone or not intro:
                    print(f'Warning: {id} has empty fields.')
                    continue

                print(f'Info: {id} is added.')
                dic = {'id': id, 'email': email,
                       'phone': phone, 'intro': intro}
                users_filtered.append(dic)
                st.add(user.get('id'))
            else:
                print(f'Warning: {user.get("id")} is repeated.')

    # 写入 `data/json/filtered/users_info.json` 文件
    with open('data/json/filtered/users_info.json', 'w', encoding='utf-8') as f:
        json_str = json.dumps(users_filtered, ensure_ascii=False)
        f.write(json_str)

    print('Done.')


if __name__ == '__main__':
    main()
