package jsonmatchers

import org.json4s.Diff
import org.json4s.JsonAST.{JNothing, JValue}
import org.json4s.jackson.JsonMethods.compact
import org.scalatest.matchers.{MatchResult, Matcher}

import scala.collection.mutable.ListBuffer

trait JsonMatchers {

  def equals(right: JValue): Matcher[JValue] = new Matcher[JValue] {
    override def apply(left: JValue): MatchResult = {
      val diffResult = Diff.diff(left, right)

      val failureMessageBuf = new ListBuffer[String]
      failureMessageBuf.append("Json elements are Different.")

      if (diffResult.changed != JNothing) {
        failureMessageBuf.append(s"  changed : right -> ${compact(diffResult.changed)}")
        failureMessageBuf.append(s"            left  -> ${compact(Diff.diff(right, left).changed)}")
      }
      if (diffResult.added != JNothing) {
        failureMessageBuf.append(s"  added : ${compact(diffResult.added)}")
      }
      if (diffResult.deleted != JNothing) {
        failureMessageBuf.append(s"  deleted : ${compact(diffResult.deleted)}")
      }

      MatchResult(
        matches = false,
        failureMessageBuf.mkString("\n"),
        "Json elements are same.")
    }

    override def toString(): String = s"Json Matchers : `${compact(right)}`"
  }

}
