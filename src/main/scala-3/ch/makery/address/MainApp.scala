package ch.makery.address

import ch.makery.address.model.Person
import ch.makery.address.util.Database
import ch.makery.address.view.{PersonEditDialogController, PersonOverviewController}
import javafx.fxml.FXMLLoader
import javafx.scene as jfxs
import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.Scene
import scalafx.scene.image.Image
import scalafx.stage.{Modality, Stage}

object MainApp extends JFXApp3:
  Database.setupDB()
  //Window Root Pane
  var roots: Option[scalafx.scene.layout.BorderPane] = None

  var cssResource = getClass.getResource("view/DarkTheme.css")
  var personOverviewController: Option[PersonOverviewController] = None

  /**
   * The data as an observable list of Persons.
   */
  val personData = new ObservableBuffer[Person]()

  /**
   * Constructor
   */
  //assign all person into personData array
  personData ++= Person.getAllPersons


  //if not is ++=

  override def start(): Unit =
    // transform path of RootLayout.fxml to URI for resource location.
    val rootResource = getClass.getResource("view/RootLayout.fxml")
    // initialize the loader object.
    val loader = new FXMLLoader(rootResource)
    // Load root layout from fxml file.
    loader.load()

    // retrieve the root component BorderPane from the FXML
    roots = Option(loader.getRoot[jfxs.layout.BorderPane])

    stage = new PrimaryStage():
      title = "AddressApp"
      icons += new Image(getClass.getResource("/images/img.png").toExternalForm)
      scene = new Scene():
        root = roots.get
        stylesheets = Seq(cssResource.toExternalForm)

    // call to display PersonOverview when app start
    showPersonOverview()
  // actions for display person overview window
  def showPersonOverview(): Unit =
    val resource = getClass.getResource("view/PersonOverview.fxml")
    val loader = new FXMLLoader(resource)
    loader.load()
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    val ctrl = loader.getController[PersonOverviewController]
    personOverviewController = Option(ctrl)
    this.roots.get.center = roots

  val stringA = new StringProperty("Hello")//publisher
  val stringB = new StringProperty("sunway")//subscriber
  val stringC = new StringProperty("sunway")

  stringA.onChange { (_, oldValue, newValue) =>
    println(s"stringA changed from $oldValue to $newValue")
  }

  stringA.onChange { (_, _, _) =>
    println(s"stringA has changed!")
  }


  stringA.value = "world"

  stringB <==> stringA
  stringC <==> stringA
  stringB.value = "google" // this will not change stringA, but stringB will be changed to "google"

  // prints "world"



  //  val func1 : Int => Int = (_) =>
  //    1
  //    println(func1(8)
  def showPersonEditDialog(person: Person): Boolean =
    val resource = getClass.getResource("view/PersonEditDialog.fxml")
    val loader = new FXMLLoader(resource)
    loader.load();
    val roots2 = loader.getRoot[jfxs.Parent]
    val control = loader.getController[PersonEditDialogController]

    val dialog = new Stage():
      initModality(Modality.ApplicationModal)
      initOwner(stage)
      scene = new Scene:
        root = roots2
        stylesheets = Seq(cssResource.toExternalForm)
    //controller has a cancel so it needs a window object,
    //behaviour of closing object is window object
    control.dialogStage = dialog
    control.person = person
    dialog.showAndWait()
    control.okClicked
//window will pop up and wait for user to click ok or cancel
//show and wait will block the main thread until the dialog is closed