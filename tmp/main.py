import yaml
from termcolor import colored

def load_and_sort_yaml(file_path):
    with open(file_path, 'r') as file:
        data = yaml.safe_load_all(file)
        return sorted(data, key=lambda item: str(item))

def compare_yaml_files(file1, file2, output_file):
    data1 = load_and_sort_yaml(file1)
    data2 = load_and_sort_yaml(file2)

    comparison_result = []

    for doc1, doc2 in zip(data1, data2):
        comparison_result.append(compare_yaml_documents(doc1, doc2))

    with open(output_file, 'w') as out_file:
        yaml.dump(comparison_result, out_file)

def compare_yaml_documents(doc1, doc2):
    keys1 = set(doc1.keys())
    keys2 = set(doc2.keys())

    common_keys = keys1.intersection(keys2)
    keys_only_in_doc1 = keys1 - common_keys
    keys_only_in_doc2 = keys2 - common_keys

    comparison = {
        'common_keys': {},
        'keys_only_in_doc1': {},
        'keys_only_in_doc2': {},
    }

    for key in common_keys:
        if doc1[key] != doc2[key]:
            comparison['common_keys'][key] = {
                'File1': doc1[key],
                'File2': doc2[key]
            }

    for key in keys_only_in_doc1:
        comparison['keys_only_in_doc1'][key] = doc1[key]

    for key in keys_only_in_doc2:
        comparison['keys_only_in_doc2'][key] = doc2[key]

    return comparison

if __name__ == '__main__':
    file1 = 'file1.yaml'  # Replace with the path to your first YAML file
    file2 = 'file2.yaml'  # Replace with the path to your second YAML file
    output_file = 'comparison_result.yaml'  # Replace with the desired output file path

    compare_yaml_files(file1, file2, output_file)
