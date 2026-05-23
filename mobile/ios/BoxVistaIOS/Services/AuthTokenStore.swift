//
//  AuthTokenStore.swift
//  BoxVistaIOS
//

import Foundation
import Security

final class AuthTokenStore {
    static let shared = AuthTokenStore()

    private let service = "hirlu.BoxVistaIOS.auth"
    private let account = "apiToken"

    private init() {}

    var token: String? {
        get { loadToken() }
        set {
            guard let newValue, !newValue.isEmpty else {
                clear()
                return
            }
            saveToken(newValue)
        }
    }

    var isAuthenticated: Bool {
        guard let token else { return false }
        return !token.isEmpty
    }

    func clear() {
        SecItemDelete(baseQuery() as CFDictionary)
    }

    private func loadToken() -> String? {
        var query = baseQuery()
        query[kSecReturnData as String] = true
        query[kSecMatchLimit as String] = kSecMatchLimitOne

        var item: CFTypeRef?
        let status = SecItemCopyMatching(query as CFDictionary, &item)
        guard status == errSecSuccess, let data = item as? Data else {
            return nil
        }

        return String(data: data, encoding: .utf8)
    }

    private func saveToken(_ token: String) {
        let data = Data(token.utf8)
        let attributes = [kSecValueData as String: data]

        let status = SecItemUpdate(baseQuery() as CFDictionary, attributes as CFDictionary)
        guard status == errSecItemNotFound else { return }

        var query = baseQuery()
        query[kSecValueData as String] = data
        query[kSecAttrAccessible as String] = kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
        SecItemAdd(query as CFDictionary, nil)
    }

    private func baseQuery() -> [String: Any] {
        [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: service,
            kSecAttrAccount as String: account
        ]
    }
}
