# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.shortcuts import render

# Create your views here.
from django.http import HttpResponse


"""

search:
	
	from python:

		dico[table_name] = dico2

		dico2[header_name] = array of header name
		dico2[result] = array 
							array of fields

	from js:

		word_to_find : String

		tables : String with comma


 queries:

 	from python:

		dico2[header_name] = array of header name
		dico2[result] = array 
							array of fields

 	from js:

 		query_num : Int , 1 to n

 		parameters : String with comma

"""

def search(request):
	context = {}

	return render(request, 'search.html', context)

def queries(request):
	context = {}

	return render(request, 'queries.html', context)
