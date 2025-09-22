#!/usr/bin/env python3
"""
Faster version using du command (Linux/Mac) or dir (Windows)
"""

import subprocess
import sys
import platform

def rge_packages_du():
    """Get large packages using du command (Linux/Mac) or dir (Windows)"""
    SIZE_THRESHOLD_MB = 100
    large_packages = []
    
    # Get package info
    try:
        print("Retrieving package list...")
        result = subprocess.run([sys.executable, '-m', 'pip', 'list', '--format', 'json'], 
                              capture_output=True, text=True, check=True)
        print("Package list retrieved.", (len(result.stdout)))
        # This requires pip 21.2+ for json format
        import json
        packages = json.loads(result.stdout)
        k = 1
        for package in packages:
            name = package['name']
            try:
                # Get package location
                show_result = subprocess.run([sys.executable, '-m', 'pip', 'show', name], 
                                           capture_output=True, text=True, check=True)
                print(f"Processing package {k}: {name}")
                k += 1
                location = None
                for line in show_result.stdout.split('\n'):
                    if line.startswith('Location:'):
                        location = line.split(':', 1)[1].strip()
                        break
                
                if location:
                    package_path = f"{location}/{name.replace('-', '_')}"
                    
                    # Get size using system commands
                    if platform.system() in ['Linux', 'Darwin']:  # Linux or Mac
                        du_result = subprocess.run(['du', '-s', '-B', '1M', package_path], 
                                                 capture_output=True, text=True)
                        if du_result.returncode == 0:
                            size_mb = int(du_result.stdout.split()[0])
                    else:  # Windows
                        # Alternative method for Windows
                        import os
                        total_size = 0
                        for dirpath, dirnames, filenames in os.walk(package_path):
                            for filename in filenames:
                                filepath = os.path.join(dirpath, filename)
                                total_size += os.path.getsize(filepath)
                        size_mb = total_size / (1024 * 1024)
                    
                    if size_mb > SIZE_THRESHOLD_MB:
                        large_packages.append((name, size_mb, package_path))
                        
            except (subprocess.CalledProcessError, FileNotFoundError):
                continue
                
    except subprocess.CalledProcessError:
        print("Error: Could not get package list")
    
    return large_packages

def main():
    print("Finding large packages (> 100 MB)...")
    
    # if platform.system() in ['Linux', 'Darwin']:
    large_packages = rge_packages_du()
    # else:
    #     # Use the first method for Windows
    #     large_packages = rge_packages()
    
    if not large_packages:
        print("No packages larger than 100 MB found.")
        return
    
    print("\nLarge packages found:")
    for package, size_mb, path in large_packages:
        print(f"{package}: {size_mb:.2f} MB")
    
    # Add uninstallation logic here similar to the first script

if __name__ == "__main__":
    main()