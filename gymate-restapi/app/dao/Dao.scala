package dao

import models.Model

// Note: Not all classes in package dao implement this interface, as some DAOs are simpler and only need a few of those methods.
trait Dao {
  def getAll: List[Model]
  def getById(id: Long): Option[Model]
  def getIdByName(name: String): Option[Long]
  def getByName(name: String): Option[Model]

  // Create and update should be implemented, but there is no abstract definition in the trait because of varying arguments
  // between models. Create/update/delete methods should generally return String "1" when the operation was performed correctly
  // and an error message otherwise.
//  def create(args: Any*): String
//  def update(args: Any*): String
  def delete(id: Long): String
}
