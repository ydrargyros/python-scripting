import os
import difflib


def file_similarity(file1, file2):
    with open(file1, 'r') as f1, open(file2, 'r') as f2:
        text1 = f1.read()
        text2 = f2.read()
        # Use difflib to compare the text content and calculate a similarity ratio
        matcher = difflib.SequenceMatcher(None, text1, text2)
        similarity = matcher.ratio()
        return similarity


def compare_directories(dir1, dir2):
    files1 = set(os.listdir(dir1))
    files2 = set(os.listdir(dir2))

    only_in_dir1 = files1 - files2
    only_in_dir2 = files2 - files1

    print(f"Files only in directory 1 ({dir1}):")
    for file in only_in_dir1:
        print(file)

    print(f"Files only in directory 2 ({dir2}):")
    for file in only_in_dir2:
        print(file)

    common_files = files1 & files2

    total_similarity = 0

    print("Similarity percentages for common files:")
    for file in common_files:
        path1 = os.path.join(dir1, file)
        path2 = os.path.join(dir2, file)
        similarity = file_similarity(path1, path2)
        total_similarity += similarity
        print(f"{file}: {similarity * 100:.2f}%")

    if common_files:
        overall_similarity = (total_similarity / len(common_files)) * 100
        print(f"Overall similarity percentage for common files: {overall_similarity:.2f}%")

if __name__ == "__main__":
    dir1 = "ours/vars"  # Replace with the actual path to your first directory
    dir2 = "theirs/vars"  # Replace with the actual path to your second directory

    compare_directories(dir1, dir2)
