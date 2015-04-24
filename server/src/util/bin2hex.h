#ifndef __BIN2HEX_H__
#define __BIN2HEX_H__
#include <string>
#include <vector>
#include <sstream>
#include <iomanip>

std::string bin2hex(const std::vector<char> &bin);

template <typename T>
std::string bin2hex(T first, T last){
	std::stringstream ss;
	while(first!=last){
		ss << std::setfill('0') << std::setw(2) << std::hex << (unsigned int) ((unsigned char) (*first));
		++first;
	}
	return ss.str();
}

template <typename T>
std::string bin2hex(T first, size_t size){
	return bin2hex((char*) first, ((char*)first)+size);
}

template <typename T>
std::string bin2hex(T first){
	return bin2hex(&first, sizeof(T));
}

std::vector<char> hex2bin(const std::string &hex);

#endif
