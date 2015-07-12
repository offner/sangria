package sangria.schema

import sangria.TestData
import sangria.TestData.{DeferFriends, CharacterRepo, Droid, Human}

object TestSchema {
  val EposideEnum = EnumType("Episode", Some("One of the films in the Star Wars Trilogy"), List(
    EnumValue("NEWHOPE", value = TestData.Eposide.NEWHOPE, description = Some("Released in 1977.")),
    EnumValue("EMPIRE", value = TestData.Eposide.EMPIRE, description = Some("Released in 1980.")),
    EnumValue("JEDI", value = TestData.Eposide.JEDI, description = Some("Released in 1983."))))

  val Character: InterfaceType[Unit, TestData.Character] = InterfaceType("Character", "A character in the Star Wars Trilogy", () => List[Field[Unit, TestData.Character]](
    Field("id", StringType, Some("The id of the character."), resolve = _.value.id),
    Field("name", OptionType(StringType), Some("The name of the character."), resolve = _.value.name),
    Field("friends", ListType(Character), Some("The friends of the character, or an empty list if they have none."), resolve = ctx => DeferFriends(ctx.value.friends)),
    Field("appearsIn", ListType(EposideEnum), Some("Which movies they appear in."), resolve = _.value.appearsIn)
  ))

  val Human = ObjectType[Unit, Human]("Human", "A humanoid creature in the Star Wars universe.", List[Field[Unit, Human]](
    Field("id", StringType, Some("The id of the human."), resolve = _.value.id),
    Field("name", OptionType(StringType), Some("The name of the human."), resolve = _.value.name),
    Field("friends", ListType(Character), Some("The friends of the human, or an empty list if they have none."), resolve = (ctx) => DeferFriends(ctx.value.friends)),
    Field("homePlanet", OptionType(StringType), Some("The home planet of the human, or null if unknown."), resolve = _.value.homePlanet)
  ), Character :: Nil)

  val Droid = ObjectType[Unit, Droid]("Droid", "A mechanical creature in the Star Wars universe.", List[Field[Unit, Droid]](
    Field("id", StringType, Some("The id of the droid."), resolve = _.value.id),
    Field("name", OptionType(StringType), Some("The name of the droid."), resolve = _.value.name),
    Field("friends", ListType(Character), Some("The friends of the droid, or an empty list if they have none."), resolve = ctx => DeferFriends(ctx.value.friends)),
    Field("primaryFunction", OptionType(StringType), Some("The primary function of the droid."), resolve = _.value.primaryFunction)
  ), Character :: Nil)

  val ID = Argument("id", StringType)

  val Query = ObjectType[CharacterRepo, Unit]("Query", List[Field[CharacterRepo, Unit]](
    Field("hero", Character, resolve = (ctx) => ctx.ctx.getHero),
    Field("human", Human, arguments = ID :: Nil, resolve = ctx => ctx.ctx.getHuman(ctx arg ID)),
    Field("droid", Droid, arguments = ID :: Nil, resolve = ctx => ctx.ctx.getDroid(ctx arg ID))
  ))

  val StarWarsSchema = Schema(Query)
}
