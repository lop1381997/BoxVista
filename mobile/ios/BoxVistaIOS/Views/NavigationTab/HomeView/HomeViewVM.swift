//
//  HomeViewVM.swift
//  BoxVistaIOS
//
//  Created by pol linger on 10/9/25.
//

import Foundation
@MainActor
class HomeViewVM: ObservableObject {
    
    @Published var boxes: [Box] = []
    @Published var isLoading: Bool = false
    @Published var errorMessage: String? = nil
    
    private let service: BoxServiceProtocol = BoxService()
    
    init()  {
        Task {
            await load()
        }
    }
    
    func load() async {
        isLoading = true
        errorMessage = nil
        do {
            boxes = try await service.getBoxes()
        } catch {
            errorMessage = "Error cargando boxes: \(error.localizedDescription)"
        }
        isLoading = false
    }

}
