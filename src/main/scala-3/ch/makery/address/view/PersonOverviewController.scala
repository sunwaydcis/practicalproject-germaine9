package ch.makery.address.view
import ch.makery.address.model.Person
import ch.makery.address.MainApp
import javafx.fxml.FXML
import javafx.scene.control.{Label, TableColumn, TableView}
import scalafx.Includes.*
import javafx.scene.control.TextField
import ch.makery.address.util.DateUtil.*
import javafx.beans.binding.Bindings
import javafx.event.ActionEvent
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

@FXML
class PersonOverviewController():
  @FXML
  private var personTable: TableView[Person] = null
  @FXML
  private var firstNameColumn: TableColumn[Person, String] = null
  @FXML
  private var lastNameColumn: TableColumn[Person, String] = null
  @FXML
  private var firstNameLabel: Label = null
  @FXML
  private var lastNameLabel: Label = null
  @FXML
  private var streetLabel: Label = null
  @FXML
  private var postalCodeLabel: Label = null
  @FXML
  private var cityLabel: Label = null
  @FXML
  private var birthdayLabel: Label = null
  // initialize Table View display contents model
  @FXML
  private var myText : TextField= null
  def initialize() =
    personTable.items = MainApp.personData
    // initialize columns's cell values
    firstNameColumn.cellValueFactory = {x => x.value.firstName}
    lastNameColumn.cellValueFactory  = {_.value.lastName}

    firstNameLabel.text <== myText.text
    showPersonDetails(None)
    personTable.selectionModel().selectedItem.onChange(
      (_, _, newValue) => showPersonDetails(Option(newValue)
    ))

  private def showPersonDetails(person: Option[Person]): Unit =
    person match
      case Some(person) => // some object has value
        // Fill the labels with info from the person object.
        firstNameLabel.text <== person.firstName
        lastNameLabel.text <== person.lastName
        streetLabel.text <== person.street
        cityLabel.text <== person.city;
        postalCodeLabel.text <== person.postalCode.delegate.asString() // postal code is an object property. can we bind object property o string property
        birthdayLabel.text <== Bindings.createStringBinding(() => {
          person.date.value.asString
        }, person.date)
  //object property change to string binding back to string property - convert to string and update the label
      case None =>
        // Person is null, remove all the text.
        firstNameLabel.text.unbind() // not a subscriber anymore, can set a value to it
        lastNameLabel.text.unbind()
        streetLabel.text.unbind()
        postalCodeLabel.text.unbind()
        cityLabel.text.unbind()
        birthdayLabel.text.unbind()

        //generate code to unbind all the following labels
        firstNameLabel.text = ""
        lastNameLabel.text = ""
        streetLabel.text = ""
        postalCodeLabel.text = ""
        cityLabel.text = ""
        birthdayLabel.text = ""
  @FXML
  def handleDeletePerson(action: ActionEvent) =
    val selectedIndex = personTable.selectionModel().selectedIndex.value
    if (selectedIndex >= 0) then
      personTable.items().remove(selectedIndex).delete()
    else
      // Nothing selected.
      val alert = new Alert(AlertType.Warning):
        initOwner(MainApp.stage) //close the alert window it goes back to the main window
        title = "No Selection"
        headerText = "No Person Selected"
        contentText = "Please select a person in the table."
      alert.showAndWait()

  @FXML
  def handleNewPerson(action: ActionEvent) =
    val person = new Person("", "")
    val okClicked = MainApp.showPersonEditDialog(person);
    if (okClicked) then {
      MainApp.personData += person
      person.save()
    }

  @FXML
  def handleEditPerson(action: ActionEvent) =
    val selectedPerson = personTable.selectionModel().selectedItem.value
    if (selectedPerson != null) then
      val okClicked = MainApp.showPersonEditDialog(selectedPerson)

      if (okClicked) then {
        showPersonDetails(Some(selectedPerson))
        selectedPerson.save()


      } else
      // Nothing selected.
        val alert = new Alert(Alert.AlertType.Warning):
          initOwner(MainApp.stage)
          title = "No Selection"
          headerText = "No Person Selected"
          contentText = "Please select a person in the table."
        .showAndWait()




