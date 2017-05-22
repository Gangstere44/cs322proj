from django.conf.urls import url
from django.views.generic.base import RedirectView

from . import views

urlpatterns = [
	url(r'^search$', views.search, name='search'),
	url(r'^queries$', views.queries, name='queries'),
	url(r'^.*$', RedirectView.as_view(pattern_name='search', permanent=False)),	
]