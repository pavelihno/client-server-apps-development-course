// SPDX-License-Identifier: MIT

pragma solidity ^0.8.0;

import "remix_tests.sol";
import "hardhat/console.sol";
import "../contracts/Election.sol";


contract ElectionTest {
    // Import the contract you want to test
    Election election;

    // Initialize the contract before each test
    function beforeAll() public {
        election = new Election();
    }

    function testVote() public {
        string memory candidateName = "Donald Trump";
        election.vote(candidateName);

        Assert.equal(election.hasVoted(address(this)), true, "Voter should have voted");

        Assert.equal(election.getWinningCandidate(), candidateName, "Candidate should have received a vote");
    }
}