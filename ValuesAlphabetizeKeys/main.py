import yaml

def recursively_sort_dict(d):
    if isinstance(d, dict):
        return {k: recursively_sort_dict(v) for k, v in sorted(d.items())}
    elif isinstance(d, list):
        return [recursively_sort_dict(v) for v in d]
    else:
        return d

def sort_helm_values(input_file, output_file):
    with open(input_file, 'r') as f:
        values = yaml.safe_load(f)

    sorted_values = recursively_sort_dict(values)

    with open(output_file, 'w') as f:
        yaml.dump(sorted_values, f, default_flow_style=False)

if __name__ == "__main__":
    input_file = "values.yaml"  # Change this to the path of your Helm chart values.yaml file
    output_file = "sorted_values.yaml"  # Change this to the desired output file

    sort_helm_values(input_file, output_file)
