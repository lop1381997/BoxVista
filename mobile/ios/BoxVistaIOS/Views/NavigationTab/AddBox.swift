//
//  ProfileView.swift
//  BoxVistaIOS
//
//  Created by pol linger on 6/6/25.
//

import SwiftUI

struct AddBox: View {
    @State private var showPicker = false
    @State private var image: UIImage?
    @State private var items: [Item] = []
    @State private var newItem: String = ""
    @ObservedObject private var boxVM = BoxVM()
    
    @State private var boxName: String = ""
    @State private var boxDescription: String = ""
    
    @State private var showAlert = false
    @State private var alertMessage = ""
    
    // Add binding for tab selection
    @Binding var selectedTab: Int
    
    var body: some View {
        NavigationView {
            VStack {
                
                Spacer()
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
                TextField("Nombre de la caja", text: $boxName)
                    .onSubmit {
                        // Aquí podrías manejar la acción de envío
                    }
                    .textInputAutocapitalization(.never)
                    .disableAutocorrection(true)
                    .padding(10)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                
                TextField("Descripción", text: $boxDescription)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .padding(10)
                
                HStack {
                    TextField("Nuevo objeto", text: $newItem)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                    Button("Agregar") {
                        guard !newItem.isEmpty else { return }
                        items.append(Item(name: newItem))
                        newItem = ""
                    }
                }.padding()
                Button("Guardar") {
                    Task {
                        await saveBoxToBackend()
                    }
                }
                .disabled(boxVM.isLoading || boxName.isEmpty)
                .padding()
                
                if boxVM.isLoading {
                    ProgressView("Guardando...")
                        .padding()
                }
            }
            .navigationTitle("BoxVista")
            .sheet(isPresented: $showPicker) {
                ImagePicker(image: $image)
            }
            .alert("Resultado", isPresented: $showAlert) {
                Button("OK") { }
            } message: {
                Text(alertMessage)
            }
            .onChange(of: boxVM.errorMessage) { _, errorMessage in
                if let error = errorMessage {
                    alertMessage = error
                    showAlert = true
                }
            }
        }
    }
    
    // Update function to navigate to HomeView after successful save
    private func saveBoxToBackend() async {
        guard !boxName.isEmpty else {
            alertMessage = "El nombre de la caja es obligatorio"
            showAlert = true
            return
        }
        
        // Convert items to ObjectItem format expected by backend
        let objects = items.map { item in
            ObjectItem(
                id: 0, // Backend will assign the ID
                nombre: item.name,
                state: item.isValid,
                boxId: 0 // Will be set by backend
            )
        }
        
        // Use BoxVM to create the box
        let createdBox = await boxVM.createBox(
            name: boxName,
            description: boxDescription,
            objects: objects
        )
        
        if let box = createdBox {
            alertMessage = "¡Caja '\(box.name)' creada exitosamente!"
            showAlert = true
            resetForm()
            
            // Navigate to HomeView after successful save
            DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
                selectedTab = 0 // Switch to Home tab
            }
        }
        // Error handling is done through BoxVM.errorMessage and onChange modifier
    }
    
    private func resetForm() {
        boxName = ""
        boxDescription = ""
        items = []
        newItem = ""
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
}

#Preview {
    AddBox(selectedTab: .constant(0))
}
