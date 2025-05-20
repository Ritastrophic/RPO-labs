import React from 'react';
import { Navbar, Nav } from 'react-bootstrap'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faHome } from '@fortawesome/free-solid-svg-icons'
import { faUser } from '@fortawesome/free-solid-svg-icons'
import { useNavigate } from 'react-router-dom';
import { Link } from 'react-router-dom';
import Utils from "../tools/Utils";
import BackendService from "../services/BackendService";
import {connect} from 'react-redux';
import { userActions } from "../tools/Rdx";

class NavigationBarClass extends React.Component {

    constructor(props) {
        super(props);
        this.goHome = this.goHome.bind(this);
        this.logout = this.logout.bind(this);
    }

    goHome() {
        this.props.navigate('home');
    }

logout() {
    BackendService.logout()
        .then(() => {
            Utils.removeUser();
            this.props.dispatch(userActions.logout())
            this.props.navigate('Login');
    })
}


render() {
//     console.log('Props in NavigationBarClass:', this.props);
//     console.log('User in NavigationBarClass:', this.props.user);
//     console.log('User login in NavigationBarClass:', this.props.user?.user?.login);
    return (
        <Navbar bg="light" expand="lg">
            <Navbar.Brand><FontAwesomeIcon icon={faHome} />{' '}My RPO</Navbar.Brand>
            <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav>
                        <Nav.Link as={Link} to="/home">Home</Nav.Link>
                        <Nav.Link onClick={this.goHome}>Another home</Nav.Link>
                        <Nav.Link onClick={this.gotoHome}>Yet another home</Nav.Link>
                    </Nav>
                </Navbar.Collapse>
            <Nav className="ms-auto">
            <Navbar.Text>{this.props.user && this.props.user.user.login}</Navbar.Text>
            { this.props.user &&
                <Nav.Link onClick={this.logout}><FontAwesomeIcon icon={faUser} fixedWidth />{' '}Выход     </Nav.Link>
            }
            { !this.props.user &&
                <Nav.Link as={Link} to="/login"><FontAwesomeIcon icon={faUser} fixedWidth />{' '}Вход     </Nav.Link>
            }
        </Nav>
        </Navbar>
    );
}

}
const NavigationBar = props => {
    const navigate = useNavigate();
    return (
        <NavigationBarClass
            navigate={navigate}
            {...props}
        />
    );
};


const mapStateToProps = state => {
    const { user } = state.authentication;
    return { user };
}

export default  connect(mapStateToProps)(NavigationBar);