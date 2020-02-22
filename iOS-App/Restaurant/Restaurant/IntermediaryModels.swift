//
//  IntermediaryModels.swift
//  Restaurant
//
//  Created by Sudhanshu Bhoi on 26/09/19.
//  Copyright © 2019 Sudhanshu Bhoi. All rights reserved.
//

import Foundation

struct Categories: Codable {
    let categories: [String]
}

struct PreparationTime: Codable {
    let prepTime: Int
    
    enum CodingKeys: String, CodingKey {
        case prepTime = "preparation_time"
    }
}
