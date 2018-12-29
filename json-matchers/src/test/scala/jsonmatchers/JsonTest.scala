package jsonmatchers

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.scalatest.{FunSpec, Matchers}

class JsonTest extends FunSpec with Matchers with JsonMatchers {
  it("compare two json") {
    val a = parse(
      """
         |{
         |  "field": "value",
         |  "field1": "value1"
         |}
       """.stripMargin)

    val b = parse(
      """
         |{
         |  "field": "value1",
         |  "field2": "value2"
         |}
       """.stripMargin)

    a should equals(b)
  }
}
