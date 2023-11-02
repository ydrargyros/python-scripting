import yaml


def compare_files(file1, file2):
    print_header()
    with open(file1) as f1, open(file2) as f2:
        data1 = yaml.safe_load(f1)
        data2 = yaml.safe_load(f2)

    overrides = {}

    for key, value in data2.items():
        if key not in data1:
            overrides[key] = value
        elif isinstance(value, dict):
            suboverrides = compare_subkeys(data1[key], value)
            if suboverrides:
                overrides[key] = suboverrides
        elif data1[key] != value:
            overrides[key] = value

    with open('overrides.yaml', 'w') as outfile:
        yaml.dump(overrides, outfile, default_flow_style=False)

def compare_subkeys(data1, data2):
    suboverrides = {}
    for key, value in data2.items():
        if key not in data1:
            suboverrides[key] = value
        elif isinstance(value, dict):
            subsuboverrides = compare_subkeys(data1[key], value)
            if subsuboverrides:
                suboverrides[key] = subsuboverrides
        elif isinstance(value, list) and isinstance(data1[key], list):
            if len(value) != len(data1[key]):
                suboverrides[key] = value
            else:
                for i, item in enumerate(value):
                    if item != data1[key][i]:
                        suboverrides[key] = value
                        break
        elif data1[key] != value:
            suboverrides[key] = value
    return suboverrides


# Header
def print_header():
    print("""
  _______ _____ _____ _____ _____ _____ 
 |__   __|_   _/ ____/ ____|_   _|  __ \\
    | |    | || |   | |  __  | | | |__) |
    | |    | || |   | | |_ | | | |  _  / 
    | |   _| || |___| |__| |_| |_| | \\ \\ 
    |_|  |______\\_____\\_____|_____|_| \\_\\
    Helm Values File Comparator v0.1a

    - by Nick Loudaros -
    """)


if __name__ == '__main__':
    compare_files('values-original.yaml', 'values-dev-overrides.yaml')
