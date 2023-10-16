import requests
import os

# Confluence credentials and space details
CONFLUENCE_URL = "https://your-confluence-instance.com"
SPACE_KEY = "YOUR_SPACE_KEY"
USERNAME = "your_username"
PASSWORD = "your_password"

# Define output directory
OUTPUT_DIR = "confluence_export"
os.makedirs(OUTPUT_DIR, exist_ok=True)

# Function to export a page and its attachments
def export_page_and_attachments(page_id, page_title, parent_path=""):
    page_url = f"{CONFLUENCE_URL}/rest/api/content/{page_id}?expand=body.storage,version"
    response = requests.get(page_url, auth=(USERNAME, PASSWORD))
    page_data = response.json()

    # Create a directory structure based on the Confluence page hierarchy
    page_path = os.path.join(OUTPUT_DIR, parent_path, page_title)
    os.makedirs(page_path, exist_ok=True)

    # Save the page content to a file
    with open(os.path.join(page_path, "page_content.html"), "w", encoding="utf-8") as page_file:
        page_file.write(page_data["body"]["storage"]["value"])

    # Download and save attachments
    for attachment in page_data["body"]["storage"]["value"].split('href="/download/attachments/')[1:]:
        attachment_id = attachment.split('"')[0]
        attachment_url = f"{CONFLUENCE_URL}/download/attachments/{attachment_id}"
        attachment_name = attachment_url.split("/")[-1]
        attachment_path = os.path.join(page_path, attachment_name)

        attachment_response = requests.get(attachment_url, auth=(USERNAME, PASSWORD))
        with open(attachment_path, "wb") as attachment_file:
            attachment_file.write(attachment_response.content)

    # Recursively process child pages
    for child in page_data["children"]["page"]:
        export_page_and_attachments(child["id"], child["title"], page_title)

# Start the export process from the root page of the Confluence space
export_page_and_attachments("root", SPACE_KEY)

print("Export completed.")