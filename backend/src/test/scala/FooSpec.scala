import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers

class FooSpec extends AnyFeatureSpec with Matchers {
  Feature("foo") {
    Scenario("test") {
      Foo.foo() shouldBe "bar"
    }
  }
}
