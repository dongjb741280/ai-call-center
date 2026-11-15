#!/usr/bin/env python3
"""Install the managed SIP dialing context for explicitly selected directory users.

Default: validate and preview. --apply writes timestamped backups before changes.
Reload FreeSWITCH XML only after the matching voxai-call version is running.
"""
import argparse
from datetime import datetime
from pathlib import Path
import re
import shutil
import xml.etree.ElementTree as ET


def plan(conf, users):
    source = Path(__file__).parent / 'conf/dialplan/voxai-agent.xml'
    ET.parse(source)
    changes = [(conf / 'dialplan/voxai-agent.xml', source.read_text())]
    for user in users:
        if not re.fullmatch(r'[0-9]+', user):
            raise ValueError('SIP user must contain digits only')
        path = conf / 'directory/default' / (user + '.xml')
        text = path.read_text()
        root = ET.fromstring(text)
        if root.find(".//user[@id='" + user + "']") is None:
            raise ValueError('Directory user does not match filename: ' + str(path))
        pattern = r'(<variable\s+name=[\'"]user_context[\'"]\s+value=[\'"])[^\'"]*([\'"])'
        updated, count = re.subn(pattern, r'\g<1>voxai-agent\2', text)
        if count != 1:
            raise ValueError('Expected exactly one user_context in ' + str(path))
        ET.fromstring(updated)
        changes.append((path, updated))
    return [(path, text) for path, text in changes if not path.exists() or path.read_text() != text]


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--conf', type=Path, required=True, help='FreeSWITCH conf_dir')
    parser.add_argument('--users', nargs='+', required=True, help='SIP users already bound to platform agents')
    parser.add_argument('--apply', action='store_true')
    args = parser.parse_args()
    changes = plan(args.conf, args.users)
    if not changes:
        print('Configuration already matches; no changes.')
        return
    for path, _ in changes:
        print(('UPDATE ' if args.apply else 'PREVIEW ') + str(path))
    if not args.apply:
        print('Validation passed. Add --apply to install; no files changed.')
        return
    backup = args.conf / '.voxai-agent-backups' / datetime.now().strftime('%Y%m%d-%H%M%S-%f')
    for path, text in changes:
        if path.exists():
            saved = backup / path.relative_to(args.conf)
            saved.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(path, saved)
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(text)
    print('Installed. Existing files backed up under ' + str(backup))
    print('After deploying voxai-call: fs_cli -x reloadxml')


if __name__ == '__main__':
    main()
