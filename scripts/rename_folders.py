import os
import re
import argparse
import shutil

def rename_folders(base_dir):
    """
    Renames folders in the base_dir matching the pattern 'mmdd_nnn' to 'mmdd_(nnn+1)'.

    Args:
        base_dir (str): The path to the base directory containing the folders.
    """
    if not os.path.isdir(base_dir):
        print(f"Error: Base directory '{base_dir}' not found.")
        return

    # Regex to identify folders like mmdd_nnn
    pattern = re.compile(r"^\d{4}_\d{3}$")

    # Collect all folders to be renamed
    folders_to_rename = []
    for folder_name in os.listdir(base_dir):
        if os.path.isdir(os.path.join(base_dir, folder_name)) and pattern.match(folder_name):
            folders_to_rename.append(folder_name)

    # Sort folders to process them in a predictable order, though the logic
    # should handle out-of-order processing if necessary by checking existing target names.
    # Sorting by name should be sufficient here.
    folders_to_rename.sort()

    # Determine the maximum existing sequence number for each mmdd prefix to avoid collisions
    # This approach is safer than just incrementing, as it handles gaps or pre-existing manually incremented folders.

    # We need to process in reverse order of sequence numbers to avoid overwriting
    # when incrementing (e.g. 0314_001 -> 0314_002, if 0314_002 already exists and needs to become 0314_003)
    # A better way is to find the highest number first or rename to temporary names.
    # For simplicity, let's assume we want to shift all existing folders.
    # A truly robust solution might involve finding all folders for a given mmdd,
    # then renaming them starting from the highest sequence number downwards.

    # Create a dictionary to store new names to avoid conflicts during the rename operation
    # Key: old_name, Value: new_name
    rename_map = {}

    # Determine target names first to detect potential conflicts
    for folder_name in folders_to_rename:
        try:
            parts = folder_name.split('_')
            mmdd = parts[0]
            seq_num_str = parts[1]

            # Increment sequence number
            seq_num = int(seq_num_str)
            new_seq_num = seq_num + 1

            # Format new sequence number with leading zeros
            new_seq_num_str = str(new_seq_num).zfill(3)
            new_folder_name = f"{mmdd}_{new_seq_num_str}"

            rename_map[folder_name] = new_folder_name

        except ValueError:
            print(f"Warning: Could not parse sequence number for folder '{folder_name}'. Skipping.")
            continue
        except Exception as e:
            print(f"An unexpected error occurred while processing folder '{folder_name}': {e}. Skipping.")
            continue

    # Check for conflicts: if a new name is already an existing folder that's also slated for rename,
    # it's a chain. If it's an existing folder NOT slated for rename, it's a conflict.
    # For simplicity, this script assumes direct increment is desired and doesn't handle complex chains
    # or pre-existing target folders that are not part of the sequence.
    # A more robust script would check os.path.exists(os.path.join(base_dir, new_folder_name))
    # and decide on a strategy (e.g. find max + 1, or skip).

    # Perform renames, from highest sequence to lowest to avoid overwriting if names are sequential (e.g. _001 to _002, _002 to _003)
    # Sorting keys in reverse ensures that if we have 0314_001 and 0314_002, 0314_002 is renamed first.
    sorted_folders_for_rename = sorted(rename_map.keys(), reverse=True)

    for old_folder_name in sorted_folders_for_rename:
        new_folder_name = rename_map[old_folder_name]
        old_path = os.path.join(base_dir, old_folder_name)
        new_path = os.path.join(base_dir, new_folder_name)

        # Final check to prevent overwriting something that shouldn't be
        if os.path.exists(new_path) and new_folder_name not in rename_map: # i.e. new_path is an existing folder NOT scheduled for rename
            print(f"Error: Target folder '{new_path}' already exists and is not part of the renaming batch. Skipping rename of '{old_folder_name}'.")
            continue

        try:
            print(f"Renaming '{old_path}' to '{new_path}'...")
            shutil.move(old_path, new_path)
            print(f"Successfully renamed '{old_folder_name}' to '{new_folder_name}'.")
        except OSError as e:
            print(f"Error renaming folder '{old_folder_name}' to '{new_folder_name}': {e}")
        except Exception as e:
            print(f"An unexpected error occurred while renaming '{old_folder_name}': {e}")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Rename folders matching 'mmdd_nnn' to 'mmdd_(nnn+1)'.")
    parser.add_argument("base_directory", help="The base directory containing the folders to rename (e.g., 'vids/').")

    args = parser.parse_args()

    # In the Docker container, this will be '/vids/'
    # For local testing, you might use a different path.
    base_directory_path = args.base_directory

    rename_folders(base_directory_path)
