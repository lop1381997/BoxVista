//
//  AddBox.swift
//  BoxVistaIOS
//
//  Created by pol linger on 6/6/25.
//

import SwiftUI

struct AddBox: View {
    @Binding var selectedTab: Int

    @StateObject private var boxVM = BoxVM()
    @State private var boxName = ""
    @State private var boxDescription = ""
    @State private var newObjectName = ""
    @State private var objects: [ObjectItem] = []
    @State private var nextObjectID: Int64 = -1
    @State private var showAlert = false
    @State private var alertMessage = ""
    @State private var isAuthenticated = AuthTokenStore.shared.isAuthenticated

    var body: some View {
        NavigationStack {
            List {
                if !isAuthenticated {
                    Section("Sesión") {
                        Label("Inicia sesión o regístrate antes de guardar cajas.", systemImage: "person.crop.circle.badge.exclamationmark")
                            .foregroundStyle(.orange)

                        Button {
                            selectedTab = 1
                        } label: {
                            Label("Abrir login/registro", systemImage: "person.badge.key")
                        }
                    }
                }

                Section("Caja") {
                    TextField("Nombre", text: $boxName)
                        .textInputAutocapitalization(.sentences)

                    TextField("Descripción", text: $boxDescription, axis: .vertical)
                        .lineLimit(2...4)
                }

                Section("Objetos") {
                    ForEach($objects) { $object in
                        Toggle(isOn: $object.state) {
                            Text(object.nombre)
                        }
                    }
                    .onDelete { indexSet in
                        objects.remove(atOffsets: indexSet)
                    }

                    HStack {
                        TextField("Nuevo objeto", text: $newObjectName)
                            .textInputAutocapitalization(.sentences)

                        Button("Agregar") {
                            addObject()
                        }
                        .disabled(trimmedNewObjectName.isEmpty)
                    }
                }

                Section {
                    Button {
                        Task { await saveBox() }
                    } label: {
                        if boxVM.isLoading {
                            ProgressView()
                                .frame(maxWidth: .infinity)
                        } else {
                            Text("Guardar")
                                .frame(maxWidth: .infinity)
                        }
                    }
                    .disabled(boxVM.isLoading)
                }
            }
            .navigationTitle("Nueva caja")
            .alert("No se pudo guardar", isPresented: $showAlert) {
                Button("OK", role: .cancel) {}
            } message: {
                Text(alertMessage)
            }
            .onAppear {
                isAuthenticated = AuthTokenStore.shared.isAuthenticated
            }
        }
    }

    private var trimmedBoxName: String {
        boxName.trimmingCharacters(in: .whitespacesAndNewlines)
    }

    private var trimmedBoxDescription: String {
        boxDescription.trimmingCharacters(in: .whitespacesAndNewlines)
    }

    private var trimmedNewObjectName: String {
        newObjectName.trimmingCharacters(in: .whitespacesAndNewlines)
    }

    private func addObject() {
        let objectName = trimmedNewObjectName
        guard !objectName.isEmpty else { return }

        objects.append(
            ObjectItem(
                id: nextObjectID,
                nombre: objectName,
                state: true,
                boxId: -1
            )
        )
        nextObjectID -= 1
        newObjectName = ""
    }

    private func saveBox() async {
        guard AuthTokenStore.shared.isAuthenticated else {
            alertMessage = "Inicia sesión o regístrate en Settings antes de guardar una caja."
            showAlert = true
            isAuthenticated = false
            return
        }

        guard !trimmedBoxName.isEmpty else {
            alertMessage = "El nombre de la caja es obligatorio."
            showAlert = true
            return
        }

        guard objects.contains(where: \.state) else {
            alertMessage = "Debe haber al menos un objeto activo en la caja."
            showAlert = true
            return
        }

        let createdBox = await boxVM.createBox(
            name: trimmedBoxName,
            description: trimmedBoxDescription,
            objects: objects
        )

        guard createdBox != nil else {
            alertMessage = boxVM.errorMessage ?? "No se pudo crear la caja."
            showAlert = true
            return
        }

        resetForm()
        selectedTab = 0
    }

    private func resetForm() {
        boxName = ""
        boxDescription = ""
        newObjectName = ""
        objects = []
        nextObjectID = -1
    }
}

#Preview {
    AddBox(selectedTab: .constant(2))
}
