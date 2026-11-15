import importlib.util
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest
import xml.etree.ElementTree as ET

SCRIPT = Path(__file__).resolve().parents[1] / 'configure-agent-dialplan.py'
spec = importlib.util.spec_from_file_location('configure', SCRIPT)
configure = importlib.util.module_from_spec(spec)
spec.loader.exec_module(configure)


class AgentDialplanTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.conf = Path(self.temp.name)
        directory = self.conf / 'directory/default'
        directory.mkdir(parents=True)
        for user in ('1001', '1002'):
            (directory / (user + '.xml')).write_text(
                '<include><user id="' + user + '"><variables>'
                '<variable name="user_context" value="default"/>'
                '</variables></user></include>')

    def run_script(self, *options):
        return subprocess.run([sys.executable, str(SCRIPT), '--conf', str(self.conf),
                               '--users', '1001', '1002', *options], check=True, capture_output=True, text=True)

    def test_preview_apply_backup_and_repeat(self):
        user = self.conf / 'directory/default/1001.xml'
        original = user.read_text()
        self.run_script()
        self.assertEqual(original, user.read_text())
        self.assertFalse((self.conf / 'dialplan/voxai-agent.xml').exists())
        self.run_script('--apply')
        self.assertIn('value="voxai-agent"', user.read_text())
        self.assertEqual(original, next(self.conf.glob('.voxai-agent-backups/*/directory/default/1001.xml')).read_text())
        self.assertEqual([], configure.plan(self.conf, ['1001', '1002']))
        root = ET.parse(self.conf / 'dialplan/voxai-agent.xml')
        self.assertIsNotNone(root.find(".//action[@application='park']"))
        self.assertIsNone(root.find(".//action[@application='bridge']"))

    def test_validates_all_users_before_any_writes(self):
        user = self.conf / 'directory/default/1001.xml'
        original = user.read_text()
        with self.assertRaises(FileNotFoundError):
            configure.plan(self.conf, ['1001', '9999'])
        self.assertEqual(original, user.read_text())
        self.assertFalse((self.conf / 'dialplan/voxai-agent.xml').exists())


if __name__ == '__main__':
    unittest.main()
