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
            .task {  await vm.load() }
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


    var body: some View {
        List {
            Section("Descripción") { Text(box.description ) }
            Section("Objetos") {
                BoxDetailObjectsView(vm: vm)
            }
        }
        .navigationTitle(box.name)
        .onAppear { vm.load(boxId: box.id) }
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
