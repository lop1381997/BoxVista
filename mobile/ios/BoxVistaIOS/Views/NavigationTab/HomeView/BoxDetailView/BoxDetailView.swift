//
//  BoxDetailView.swift
//  BoxVistaIOS
//
//  Created by pol linger on 10/9/25.
//
import SwiftUI

struct BoxDetailView: View {
    let box: Box
    @StateObject private var vm  = BoxDetailViewVM()
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
            .task { await vm.load(boxID: box.id) }
            
            // Improved button styling
            HStack(spacing: 20) {
                Button("Borrar") {
                    showConfirmation = true
                }
                .frame(maxWidth: .infinity)
                .padding(.vertical, 12)
                .background(Color.red)
                .foregroundColor(.white)
                .cornerRadius(10)
                .font(.system(size: 16, weight: .semibold))
                .confirmationDialog("Estas seguro de borrar la caja?", isPresented: $showConfirmation)
                {
                    Button("Borrar", role: .destructive) {
                        Task {
                            let ok = await vm.deleteBox()
                            showConfirmation = false
                            if ok { dismiss() }
                        }
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
    @ObservedObject var vm: BoxDetailViewVM
    
    var body: some View {
        ForEach(vm.objects, id: \.id) { object in
            HStack {
                Toggle(isOn: Binding(
                    get: { object.state },
                    set: { newValue in
                        vm.updateObjectState(id: object.id, to: newState(newValue, current: object.state))
                    }
                )) {
                    Text(object.nombre)
                }
            }
        }
    }
    
    // Helper to avoid stale value warnings in Toggle bindings
    private func newState(_ value: Bool, current: Bool) -> Bool { value }
}
