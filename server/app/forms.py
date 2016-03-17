from django import forms

class autoConfirmForm(forms.Form):
    confirmText = forms.CharField(label='confirmStringInput', max_length=30)