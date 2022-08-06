package info.cepheus.sample

import io.quarkus.test.junit.NativeImageTest

@NativeImageTest
class NativeGreetingResourceIT : GreetingResourceTest()