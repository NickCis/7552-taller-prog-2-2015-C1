#include "../catch.hpp"
#include "../../src/db/db_comparator.h"

using namespace std;
using namespace rocksdb;

TEST_CASE( "Clase comparator compare correctamente", "[DbComparator]" ) {
	DbComparator comparator;

	SECTION( "Comparaciones de igual" ) {
		REQUIRE( comparator.Compare(Slice("aaaa"), Slice("aaaa")) == 0);
		REQUIRE( comparator.Compare(Slice("aaaa/asdasd/assd"), Slice("aaaa/asdasd/assd")) == 0);
	}

	SECTION( "Comparaciones de mayor/menor" ) {
		REQUIRE( comparator.Compare(Slice("aaa"), Slice("aaaa")) < 0);
		REQUIRE( comparator.Compare(Slice("aaaa"), Slice("aaa")) > 0);

		REQUIRE( comparator.Compare(Slice("aaaaa"), Slice("aaabb")) < 0);
		REQUIRE( comparator.Compare(Slice("aaabb"), Slice("aaaaa")) > 0);

		REQUIRE( comparator.Compare(Slice("aaaaaa"), Slice("aaabb")) < 0);
		REQUIRE( comparator.Compare(Slice("aaabb"), Slice("aaaaaa")) > 0);

		REQUIRE( comparator.Compare(Slice("aaaa/aaa"), Slice("aaaa/bbb")) < 0);
		REQUIRE( comparator.Compare(Slice("aaaa/bbb"), Slice("aaaa/aaa")) > 0);

		REQUIRE( comparator.Compare(Slice("aaaa/aaa"), Slice("aaaa/aaaa")) < 0);
		REQUIRE( comparator.Compare(Slice("aaaa/aaaa"), Slice("aaaa/aaa")) > 0);

		REQUIRE( comparator.Compare(Slice("aaaaaa/bbbb"), Slice("aaabb/aaaa")) < 0);
		REQUIRE( comparator.Compare(Slice("aaabb/aaaa"), Slice("aaaaaa/bbbb")) > 0);

		REQUIRE( comparator.Compare(Slice("a/bbbb"), Slice("aaabb/aaaa")) < 0);
		REQUIRE( comparator.Compare(Slice("aaabb/aaaa"), Slice("a/bbbb")) > 0);
	}
}
