from django import forms


class CommentReplyForm(forms.Form):
    reply = forms.CharField(max_length=100, min_length=6,
                            widget=forms.Textarea(attrs={"class": "reply-content"}),
                            label=None
                            )
