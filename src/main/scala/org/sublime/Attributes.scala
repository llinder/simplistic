package org.sublime

import Conversions._

object Attributes {

  trait NamedAttribute {
    val name: String
  }

  /**
   * An attribute is a definition of a named value with a conversion to and from a string
   * representation.  It is useful when working with structures such as http request
   * parameters and simpledb queries.
   */
  case class Attribute[T](
    override val name: String,
    conversion: Conversion[T]
  ) extends NamedAttribute {

    /** When applied to a value, the conversion returns a name value pair of strings */
    def apply(value: T) = (name -> conversion(value))

    /**
     * When applied to a map of names to sets of string values, the conversion returns a
     * list of values retrieved from the set and converted from strings back to their
     * original type.
     */
    def apply(result: Map[String, Set[String]]): List[T] =
      if (! result.contains(name)) List.empty
      else (result(name) flatMap (_ match {
        case conversion(value) => List(value)
        case _ => List.empty
      })).toList
  }

  /** Create a simple attribute which performs no conversion on string values. */
  def attribute(name: String) = Attribute(name, Conversions.PassThrough)

  /** Create a typed attribute with an associated conversion to and from that type. */
  def attribute[T](name: String, conversion: Conversion[T]) = Attribute[T](name, conversion)
}
