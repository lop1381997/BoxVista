//
//  Home Screen.swift
//  BoxVistaIOS
//
//  Created by pol linger on 6/6/25.
//

import SwiftUI



struct HomeView: View {
    @ObservedObject private var vm = BoxVM()
    
    private var allObjects: [ObjectItem] {
         vm.boxes.flatMap { $0.objects }
    }

    var body: some View {
        NavigationStack {
            List {
                Section("Cajas") {
                    ForEach(vm.boxes) { box in
                        NavigationLink(value: box) {
                            BoxRow(box: box)
                        }
                    }
                }
                Section("Objetos (todas las cajas)") {
                    ForEach(allObjects, id: \.id) { object in
                        Text(object.nombre)
                    }
                }
            }
            .navigationTitle("Boxes")
            .navigationDestination(for: Box.self) { box in
                BoxDetailView(box: box)
            }
            .task {   await vm.load() }
        }
    }
}

private struct BoxRow: View {
    let box: Box
    var body: some View {
        VStack(alignment: .leading, spacing: 2) {
            Text(box.name).font(.headline)
            Text((box.description)).font(.subheadline).foregroundColor(.secondary)
        }
        .padding(.vertical, 4)
        
    }
}


struct BoxDetailView: View {
    let box: Box
    @ObservedObject private var vm  = ObjectVM()
    @ObservedObject private var boxVM = BoxVM()
    @State private var showConfirmation = false
    @State private var showEdit = false
    @Environment(\.dismiss) private var dismiss
    var body: some View {
        VStack(spacing: 0) {
            List {
                Section("Descripción") { Text(box.description ) }
                Section("Objetos") {
                    BoxDetailObjectsView(vm: vm)
                }
            }
            .navigationTitle(box.name)
            .task { await vm.load(boxId: box.id) }
            
            // Improved button styling
            HStack(spacing: 20) {
                Button("Borrar") {
                    // Aquí podrías implementar la lógica para eliminar el objeto
                    print("Eliminar objeto \("nombre")")
                    showConfirmation = true
                    //
                    
                }
                .frame(maxWidth: .infinity)
                .padding(.vertical, 12)
                .background(Color.red)
                .foregroundColor(.white)
                .cornerRadius(10)
                .font(.system(size: 16, weight: .semibold))
                .confirmationDialog("Estas segurop de borrar la caja?", isPresented: $showConfirmation)
                {
                    Button("Borrar", role: .destructive) {
                        Task {
                            await boxVM.deleteBox(box)
                        }
                        showConfirmation = false
                        dismiss() // Cierra la vista después de borrar
                        
                    }
                    Button("Cancelar", role: .cancel) {
                        //cancelar
                        
                        
                    }
                    
                }message: {
                    Text("Estas segurop de borrar la caja? \nEsta acción no se puede deshacer.")
                }
                .sheet(isPresented: $showEdit) {
                    UpdateBox(box: box)
                }
                
                Button("Editar") {
                    showEdit = true
                }
                .frame(maxWidth: .infinity)
                .padding(.vertical, 12)
                .background(Color.blue)
                .foregroundColor(.white)
                .cornerRadius(10)
                .font(.system(size: 16, weight: .semibold))
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 16)
            .background(Color(.systemGray6))
            .shadow(color: Color.black.opacity(0.1), radius: 2, x: 0, y: -1)
        }
    }
}

struct BoxDetailObjectsView: View {
    @ObservedObject  var vm : ObjectVM
    
    var body: some View {
        ForEach(vm.objects) { object in
            HStack {
                Toggle(isOn: Binding(
                    get: { object.state },
                    set: { newValue in
                        vm.updateObjectState(id: object.id, to: newValue)
                    }
                )) {
                    Text(object.nombre)
                }
                
            }
        }
    }
}



#Preview {
    HomeView()
}
