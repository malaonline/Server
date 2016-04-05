from django.shortcuts import render
from django.views.generic import View


def get_git_revision_hash():
    import subprocess
    return subprocess.check_output(['git', 'rev-parse', 'HEAD'])


class Index(View):
    def get(self, request):
        # 官网的初始页面
        return render(
                request, 'web/index.html', dict(rev=get_git_revision_hash()))
