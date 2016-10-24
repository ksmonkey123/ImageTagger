package ch.awae.imgtagger

import scala.collection.immutable.HashMap
import java.io.Serializable

@SerialVersionUID(0L)
case class PersistenceContainer(val data: HashMap[String, Serializable])