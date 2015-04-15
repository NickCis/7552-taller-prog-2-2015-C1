#include "bin2hex.h"

#include <sstream>
#include <iomanip>

using std::hex;
using std::setw;
using std::vector;
using std::string;
using std::setfill;
using std::stringstream;

string bin2hex(const vector<char> &bin){
	stringstream ss;

	for(auto it = bin.begin(); it != bin.end(); it++)
		ss << setfill('0') << setw(2) << hex << (unsigned int) ((unsigned char) (*it));

	return ss.str();
}
