// SPDX-License-Identifier: MIT

pragma solidity ^0.8.0;


contract Election {
    
    struct Candidate {
        uint id;
        string name;
        uint voteCount;
    }

    // Проголосовавшие избиратели
    mapping(address => bool) private voters;

    // Кандидаты
    Candidate[] private candidates;

    // Событие "Кандидат проголосовал"
    event Voted(uint candidateId);

    constructor() {
        addCandidate("Joe Biden");
        addCandidate("Donald Trump");
    }

    // Добавление кандидата
    function addCandidate(string memory _name) private {
        uint candidateId = candidates.length;
        candidates.push(Candidate(candidateId, _name, 0));
    }

    // Поиск id кандидата по его имени
    function findCandidateId(string memory _candidateName) private view returns (uint) {
        for (uint i = 0; i < candidates.length; i++) {
            if (keccak256(bytes(candidates[i].name)) == keccak256(bytes(_candidateName))) {
                return candidates[i].id;
            }
        }
        return 0;
    }

    function vote(string memory _candidateName) public {
        // Избиратель ранее уже проголосовал 
        require(!voters[msg.sender], "You have already voted");

        // Кандидат существует
        uint _candidateId = findCandidateId(_candidateName);
        require(_candidateId < candidates.length, "Invalid candidate ID");

        // Избиратель проголосовал
        voters[msg.sender] = true;

        // Добавление голоса за кандидата
        candidates[_candidateId].voteCount++;

        // Вызов события
        emit Voted(_candidateId);
    }

    function hasVoted(address _voter) public view returns (bool) {
        return voters[_voter];
    }

    function getCandidates() public view returns (string[] memory) {
        string[] memory candidateNames = new string[](candidates.length);
        for (uint i = 0; i < candidates.length; i++) {
            candidateNames[i] = candidates[i].name;
        }
        return candidateNames;
    }

    function getWinningCandidate() public view returns (string memory) {
        require(candidates.length > 0, "No candidates in the election");

        Candidate memory winningCandidate = candidates[0];

        for (uint i = 1; i < candidates.length; i++) {
            if (candidates[i].voteCount > winningCandidate.voteCount) {
                winningCandidate = candidates[i];
            }
        }

        return winningCandidate.name;
    }
}