#if canImport(SwiftUI)
import SwiftUI
import UIKit

struct Item: Identifiable {
    let id = UUID()
    var name: String
    var isValid: Bool = false
}

@main
struct BoxVistaIOSApp: App {
    @State private var showPicker = false
    @State private var image: UIImage?
    @State private var items: [Item] = []
    @State private var newItem: String = ""

    var body: some Scene {
        WindowGroup {
            NavigationView {
                VStack {
                    if let image = image {
                        Image(uiImage: image)
                            .resizable()
                            .scaledToFit()
                            .frame(height: 200)
                    }
                    Button("Tomar Foto") { showPicker = true }
                        .padding()

                    HStack {
                        TextField("Nuevo objeto", text: $newItem)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                        Button("Agregar") {
                            guard !newItem.isEmpty else { return }
                            items.append(Item(name: newItem))
                            newItem = ""
                        }
                    }.padding()

                    List {
                        ForEach($items) { $item in
                            HStack {
                                Toggle(isOn: $item.isValid) {
                                    Text(item.name)
                                }
                            }
                        }.onDelete { indexSet in
                            items.remove(atOffsets: indexSet)
                        }
                    }
                    Spacer()
                    Button("Guardar Cambios") {
                        // En el futuro se enviará la lista al backend
                    }
                    .padding()
                }
                .navigationTitle("BoxVista")
                .sheet(isPresented: $showPicker) {
                    ImagePicker(image: $image)
                }
            }
        }
    }
}

struct ImagePicker: UIViewControllerRepresentable {
    @Binding var image: UIImage?

    class Coordinator: NSObject, UINavigationControllerDelegate, UIImagePickerControllerDelegate {
        let parent: ImagePicker
        init(parent: ImagePicker) { self.parent = parent }
        func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
            parent.image = info[.originalImage] as? UIImage
            picker.dismiss(animated: true)
        }
        func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
            picker.dismiss(animated: true)
        }
    }

    func makeCoordinator() -> Coordinator {
        Coordinator(parent: self)
    }

    func makeUIViewController(context: Context) -> UIImagePickerController {
        let picker = UIImagePickerController()
        picker.delegate = context.coordinator
        return picker
    }

    func updateUIViewController(_ uiViewController: UIImagePickerController, context: Context) {}
}

#else
@main
struct BoxVistaCLI {
    static func main() {
        print("SwiftUI not available - build for iOS.")
    }
}
#endif
