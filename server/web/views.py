from django.shortcuts import render
from django.views.generic import View


def get_git_revision_hash():
    import subprocess
    return subprocess.check_output(['git', 'rev-parse', 'HEAD'])


class PatriarchIndex(View):
    def get(self, request):
        return render(
                request, 'web/patriarch_index.html', dict(rev=get_git_revision_hash()))

class TeacherIndex(View):
    def get(self, request):
        return render(
                request, 'web/teacher_index.html', dict(rev=get_git_revision_hash()))
