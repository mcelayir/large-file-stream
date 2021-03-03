package com.mcelayir.largefile

import org.apache.kafka.common.serialization.{Deserializer, Serializer}

import scala.util.{Failure, Success, Try}
import play.api.libs.json.Json

case class Product(id: Int, country: String)

case class EnrichedProduct(id: Int, countryCount: Map[String, Int])


class ProductSerializer extends Serializer[Product] {
  implicit val writes = Json.writes[Product]

  override def serialize(topic: String, data: Product): Array[Byte] =
    Json.toJson(data).toString().getBytes
}

class ProductDeserializer extends Deserializer[Product] {
  implicit val reads = Json.reads[Product]

  override def deserialize(topic: String, data: Array[Byte]): Product = {
    Json.fromJson[Product](Json.parse(data)).get
  }
}

object ProductFactory {
  def fromCsvRow(row: Seq[String]): Option[Product] = Try {
    Product(
      row(0).toInt,
      row(1)
    )
  } match {
    case Success(value) => Some(value)
    case Failure(exception) =>
      println(s"Dropping row: ${row.mkString(",")}. Exception $exception")
      None
  }
}
