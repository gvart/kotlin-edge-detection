package md.gva.sp

import javafx.application.Application
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.image.Image
import javafx.stage.FileChooser
import tornadofx.*

//fun main(args: Array<String>) {
//    Application.launch(Launcher::class.java, *args)
//}

class Main: View(title = "Edge detection") {
    val edgeDetector = EdgeDetector()
    var imageChanged = false

    override val root = vbox(alignment = Pos.TOP_CENTER) {
        menubar {
            menu("File") {
                item("Save") {
                    action {
                        chooseFile("Save rendered picture", mode = FileChooserMode.Save, filters = arrayOf(FileChooser.ExtensionFilter("png images", "*.png")))
                    }
                }
                item("Exit") {
                    action { System.exit(1) }
                }
            }
            menu("Help") {
                item("About"){
                    action {
                        alert(type = Alert.AlertType.INFORMATION, header = "Developed by Gladis Vladlen ULIM ,TI-35")
                    }
                }
            }
        }
        vbox(spacing = 5) {
            padding = Insets(5.0)
            hbox {
                vbox(spacing = 5, alignment = Pos.TOP_CENTER) {
                    label("Source")
                    val sourceImageView = imageview {
                        fitHeight = 400.0
                        fitWidth = 400.0
                    }
                    button("Choose image") {
                        action {
                            val file = chooseFile("Select image", filters = arrayOf(FileChooser.ExtensionFilter("png images", "*.png")))
                            if(file.isNotEmpty()) {
                                val image = Image(file[0].inputStream())
                                sourceImageView.image = image
                                edgeDetector.stream = file[0].inputStream()

                                imageChanged = true
                            }
                        }
                    }
                }
                separator(Orientation.VERTICAL)
                vbox(spacing = 5, alignment = Pos.TOP_CENTER) {
                    label("Rendered")
                    val renderImageView = imageview {
                        fitHeight = 400.0
                        fitWidth = 400.0
                    }
                    button("Detect edges") {
                        action {
                            if(!imageChanged) {
                                alert(Alert.AlertType.WARNING, header = "Source image already rendered")
                            }else {
                                if (edgeDetector.ready()) {
                                    renderImageView.image = edgeDetector.render()
                                    imageChanged = false
                                } else {
                                    alert(Alert.AlertType.WARNING, header = "Source image not found")
                                }
                            }
                        }
                    }
                }

            }

        }
    }

}