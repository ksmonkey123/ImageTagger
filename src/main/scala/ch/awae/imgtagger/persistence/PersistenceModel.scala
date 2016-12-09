package ch.awae.imgtagger
package persistence

final case class PersistenceRoot(version: String, images: List[PerImage], meta: PerMeta)

/* IMAGE MODEL */
final case class PerImage(name: String, tags: List[PerImageTag])
final case class PerImageTag(value: String)

/* META MODEL (ATM EMPTY) */
final case class PerMeta()