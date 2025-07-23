//
//  UpdateBox.swift
//  BoxVistaIOS
//
//  Created by pol linger on 19/7/25.
//


import SwiftUI

struct UpdateBox: View {
    
    let box: Box

    @State private var items: [Item] = []
    @State private var newItem: String = ""
    @ObservedObject private var boxVM = BoxVM()
    
    @State private var boxName: String = ""
    @State private var boxDescription: String = ""
    
    @State private var showAlert = false
    @State private var showEdit = false
    @State private var alertMessage = ""
    
    @Environment(\.dismiss) private var dismiss

    init(box: Box) {
        self.box = box
        _boxName = State(initialValue: box.name)
        _boxDescription = State(initialValue: box.description)
        _items = State(initialValue: box.objects.map { Item(name: $0.nombre, isValid: true) })
    }
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                ScrollView {
                    VStack(alignment: .leading, spacing: 16) {
                        ForEach($items) { $item in
                            HStack {
                                Toggle(isOn: $item.isValid) {
                                    Text(item.name)
                                }
                            }
                        }
                        .onDelete { indexSet in
                            items.remove(atOffsets: indexSet)
                        }

                        
                    }
                    .padding()
                    .frame(maxHeight: .infinity, alignment: .top)
                }

                VStack(spacing: 12) {
                    TextField("Nombre de la caja", text: $boxName)
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
                    }

                    Button("Guardar Cambios") {
                        Task {
                            await updateBox()
                            if boxVM.errorMessage == nil {
                                alertMessage = "Caja actualizada correctamente."
                            } else {
                                alertMessage = boxVM.errorMessage ?? "Error al actualizar la caja."
                            }
                            showAlert = true
                            resetForm()
                        }
                    }
                    .disabled(boxVM.isLoading || boxName.isEmpty)

                    if boxVM.isLoading {
                        ProgressView("Guardando...")
                    }
                }
                .padding()
                .background(Color(UIColor.systemBackground))
            }
            .ignoresSafeArea(.keyboard, edges: .bottom)
            .navigationTitle("BoxVista")
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
    
    private func updateBox() async{
        guard !boxName.isEmpty else {
            alertMessage = "El nombre de la caja no puede estar vacío."
            showAlert = true
            return
        }
        
        // Validar que al menos un objeto esté activo
        guard items.contains(where: { $0.isValid }) else {
            alertMessage = "Debe haber al menos un objeto activo en la caja."
            showAlert = true
            return
        }
        //vrear caja para actualizar
        let boxupdated = Box(
            id: self.box.id,
            name: boxName,
            description: boxDescription,
            objects: items.map { ObjectItem(id: 0, nombre: $0.name, state: $0.isValid, boxId: 0) }
        )
        
        _ = await boxVM.updateBox(boxupdated)
        
        dismiss() // Cierra la vista después de actualizar
    }
    
    private func resetForm() {
        boxName = ""
        boxDescription = ""
        items = []
        newItem = ""
    }
    
    
     
}

#Preview {
    AddBox(selectedTab: .constant(0))
}
