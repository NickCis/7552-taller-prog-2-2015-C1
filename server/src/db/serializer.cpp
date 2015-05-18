#include "serializer.h"

#include <cstring>
#include <cstdlib>

using std::copy;
using std::string;
using std::vector;
using std::strlen;
using std::calloc;

Ignore::Ignore() :
	_size(0) {}

Ignore::Ignore(const char*& str) :
	_size(strlen(str)) {}

size_t Ignore::size(){
	return this->_size;
}

OSerializer::OSerializer(string &b, size_t size) :
	buffer(b)
{
	if(size > 0)
		this->buffer.reserve(size);
	this->buffer.resize(0);
}

OSerializer& OSerializer::operator<<(const string& str){
	size_t size = str.size();
	(*this) << size;

	buffer.reserve(buffer.size()+str.size());
	buffer.append(str.begin(), str.end());
	return (*this);
}

OSerializer& OSerializer::operator<<(const vector<char>& str){
	size_t size = str.size();
	(*this) << size;

	buffer.reserve(buffer.size()+str.size());
	buffer.append(str.begin(), str.end());
	return (*this);
}

OSerializer& OSerializer::operator<<(const char* & str){
	size_t size = strlen(str);
	(*this) << size;

	buffer.reserve(buffer.size()+size);
	buffer.append(str, size);
	return (*this);
}

OSerializer& OSerializer::operator<<(ConstStrNoPrefix& str){
	buffer.reserve(buffer.size()+str->size());
	buffer.append(str->begin(), str->end());
	return (*this);
}

ISerializer::ISerializer(const string &b) :
	buffer(b),
	it(b.begin()),
	bufferSize(b.size()),
	_error(false)
{
}

bool ISerializer::error(){
	return this->_error;
}

ISerializer& ISerializer::operator>>(string& str){
	size_t size;
	(*this) >> size;

	if(! this->errorCheck(size)){
		str.resize(0);
		str.reserve(size);
		str.append(it, it+size);
		this->advance(size);
	}

	return (*this);
}

ISerializer& ISerializer::operator>>(vector<char>& str){
	size_t size;
	(*this) >> size;

	if(! this->errorCheck(size)){
		str.resize(size);
		copy(it, it+size, str.begin());
		this->advance(size);
	}

	return (*this);
}

ISerializer& ISerializer::operator>>(char* & str){
	size_t size;
	(*this) >> size;

	if(! this->errorCheck(size)){
		str = (char*) calloc(size+1, sizeof(char));
		copy(it, it+size, str);
		this->advance(size);
	}

	return (*this);
}

ISerializer& ISerializer::operator>>(StrNoPrefix str){
	if(! this->errorCheck(str.size())){
		str->resize(0);
		str->reserve(str.size());
		str->append(it, it+str.size());
		this->advance(str.size());
	}

	return (*this);
}

ISerializer& ISerializer::operator>>(Ignore ig){
	if(! this->errorCheck(ig.size())){
		this->advance(ig.size());
	}

	return (*this);
}

bool ISerializer::errorCheck(size_t size){
	if(this->_error || this->bufferSize < size)
		return (this->_error = true);

	return false;
}

void ISerializer::advance(size_t size){
	this->it += size;
	this->bufferSize -= size;
}
