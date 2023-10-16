import paramiko
import os
import yaml
from k8sconfigutils import sanitize

# Map of environment names and IPs
product_name = "icarus"
product_handler = "irm"
environment_map = {
    "dev": "10.240.36.41",
    "qa": "10.240.36.79",
    "stg": "10.240.36.80",
    "prf": "10.240.171.35",
    "e2e": "10.240.36.172",
    "hfx": "10.240.36.227",
    "prf": "10.240.171.35"
}

# SSH username and private key path
ssh_username = "root"
private_key_path = "./id_rsa"

# Local directory to store .kube/config files
local_directory = "./output"

# Connect to each environment's IP and copy the .kube/config file
for environment, ip in environment_map.items():
    try:
        # SSH connection
        ssh = paramiko.SSHClient()
        ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
        ssh.connect(ip, username=ssh_username, key_filename=private_key_path)

        # Remote file path
        remote_file_path = ".kube/config"

        # Local file path
        yaml_file_path = os.path.join(local_directory, f"{product_handler}{environment}.kube.config")

        # Copy the file
        ftp = ssh.open_sftp()
        ftp.get(remote_file_path, yaml_file_path)
        ftp.close()

        # Read the file contents
        with open(yaml_file_path, "r") as file:
            yaml_content = yaml.safe_load(file)
            # print(yaml_content)

        new_cluster_name = f"{environment}.{product_name}"
        new_server_ip = f"https://{ip}:6443"
        new_context_cluster = new_cluster_name
        sanitize.run(yaml_content, new_cluster_name, new_server_ip, new_context_cluster)

        # Write the file
        with open(yaml_file_path, "w") as file:
            yaml.dump(yaml_content, file)
        # Disconnect SSH
        ssh.close()

        print(f"Config file copied from {environment} ({ip}) to {yaml_file_path}")

    except Exception as e:
        print(f"Failed to copy config file from {ip}: {str(e)}")
