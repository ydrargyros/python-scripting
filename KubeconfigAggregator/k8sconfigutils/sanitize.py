# Sanitize .kube/config files of each cluster

def run(yaml_content, new_cluster_name, new_server_ip, new_context_cluster):

    # Update the 'cluster.name'
    for cluster_entry in yaml_content.get('clusters', []):
        cluster_entry['name'] = new_cluster_name

    # Update the 'context.cluster'
    for clusters_entry in yaml_content.get('clusters', []):
        if 'cluster' in cluster_entry:
            cluster = cluster_entry['cluster']
            if 'server' in cluster:
                cluster['server'] = new_server_ip

    # Update the 'context.cluster'
    for context_entry in yaml_content.get('contexts', []):
        if 'context' in context_entry:
            context = context_entry['context']
            if 'cluster' in context:
                context['cluster'] = new_context_cluster




#       Replace all "kubernetes-admin@cluster.local" occurences
#     contexts:
#     - context:
#     cluster: dev.icarus
#     user: kubernetes - admin <<<< USER MUST BE SOMETHING MORE MEANINGFUL
#
#
# name: kubernetes - admin @ cluster.local
# current - context: kubernetes - admin @ cluster.local
# kind: Config
# preferences: {}
# users:
# - name: kubernetes - admin
# user: