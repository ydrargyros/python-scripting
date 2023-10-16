# Merge kubeconfigs

import os
import glob
from kubernetes import client, config

# Directory where kubeconfig files are located
kubeconfig_dir = "~/output"

# Expand the '~' symbol in the directory path
kubeconfig_dir = kubeconfig_dir.replace("~", "/home/your_username")

# Find all ".config" files in the directory
kubeconfig_files = glob.glob(os.path.join(kubeconfig_dir, "*.config"))

if not kubeconfig_files:
    print("No kubeconfig files found in the specified directory.")
    exit(1)

# Load the kubeconfig files
config.load_kube_config(config_file=kubeconfig_files)

# Get the flattened kubeconfig
kubeconfig = config.list_kube_config_contexts(config_file=None, show_all=False, client_configuration=False)

# Save the flattened kubeconfig to a temporary file
with open('/home/your_username/.kube/config_tmp', 'w') as tmp_file:
    tmp_file.write(kubeconfig)

print("Flattened kubeconfig saved to ~/.kube/config_tmp")